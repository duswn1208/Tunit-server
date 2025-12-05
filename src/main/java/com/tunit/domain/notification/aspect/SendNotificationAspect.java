package com.tunit.domain.notification.aspect;

import com.tunit.domain.notification.annotation.SendNotification;
import com.tunit.domain.notification.dto.PushNotificationDto;
import com.tunit.domain.notification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;

/**
 * @SendNotification 어노테이션을 처리하는 AOP
 * 트랜잭션이 성공적으로 커밋된 후에만 알림을 전송합니다
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SendNotificationAspect {

    private final PushNotificationService pushNotificationService;
    private final ExpressionParser parser = new SpelExpressionParser();

    @AfterReturning(pointcut = "@annotation(com.tunit.domain.notification.annotation.SendNotification)", returning = "result")
    public void sendNotification(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            SendNotification annotation = method.getAnnotation(SendNotification.class);

            if (annotation == null) {
                return;
            }

            // 트랜잭션이 활성화되어 있는지 확인
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                // 트랜잭션 커밋 후 실행되도록 등록
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        processSendNotification(joinPoint, result, method, annotation);
                    }
                });
                log.debug("알림 전송이 트랜잭션 커밋 후로 예약됨 - Method: {}", method.getName());
            } else {
                // 트랜잭션이 없으면 즉시 실행
                log.warn("트랜잭션 없이 @SendNotification 실행 - Method: {}", method.getName());
                processSendNotification(joinPoint, result, method, annotation);
            }

        } catch (Exception e) {
            log.error("@SendNotification 처리 중 오류 발생", e);
        }
    }

    private void processSendNotification(JoinPoint joinPoint, Object result, Method method, SendNotification annotation) {
        try {
            // SpEL 컨텍스트 생성
            StandardEvaluationContext context = new StandardEvaluationContext();

            // 메서드 파라미터를 컨텍스트에 추가
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }

            // 메서드 반환값을 컨텍스트에 추가
            context.setVariable("result", result);
            context.setRootObject(result);

            // SpEL로 값 추출
            Long userNo = extractLong(annotation.userNoField(), context);
            String title = extractString(annotation.title(), context);
            String message = extractString(annotation.message(), context);
            String deepLink = annotation.deepLink().isEmpty() ? null : extractString(annotation.deepLink(), context);
            String imageUrl = annotation.imageUrl().isEmpty() ? null : extractString(annotation.imageUrl(), context);

            if (userNo == null) {
                log.warn("UserNo를 추출할 수 없습니다. expression: {}", annotation.userNoField());
                return;
            }

            // 알림 전송
            PushNotificationDto dto = PushNotificationDto.builder()
                    .userNo(userNo)
                    .notificationType(annotation.type())
                    .title(title)
                    .message(message)
                    .deepLink(deepLink)
                    .imageUrl(imageUrl)
                    .build();

            pushNotificationService.sendPushNotification(dto);

            log.info("@SendNotification 처리 완료 (트랜잭션 커밋 후) - Method: {}, UserNo: {}, Type: {}",
                    method.getName(), userNo, annotation.type());

        } catch (Exception e) {
            log.error("알림 전송 처리 중 오류 발생", e);
        }
    }

    private Long extractLong(String expression, StandardEvaluationContext context) {
        try {
            Object value = parser.parseExpression(expression).getValue(context);
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                return Long.parseLong((String) value);
            }
        } catch (Exception e) {
            log.error("Long 값 추출 실패: {}", expression, e);
        }
        return null;
    }

    private String extractString(String expression, StandardEvaluationContext context) {
        try {
            // SpEL 표현식인지 확인 (#{...} 형태)
            if (expression.contains("#{") && expression.contains("}")) {
                // #{...} 제거하고 평가
                String spelExpression = expression.replaceAll("#\\{([^}]+)\\}", "$1");
                return parser.parseExpression(spelExpression).getValue(context, String.class);
            } else if (expression.startsWith("#")) {
                // #variable 형태
                return parser.parseExpression(expression).getValue(context, String.class);
            } else {
                // 일반 문자열
                return expression;
            }
        } catch (Exception e) {
            log.error("String 값 추출 실패: {}", expression, e);
            return expression; // 실패 시 원본 반환
        }
    }
}

