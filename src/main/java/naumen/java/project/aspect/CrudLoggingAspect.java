package naumen.java.project.aspect;

import naumen.java.project.service.ExternalLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CrudLoggingAspect {

    private final ExternalLogService logService;

    public CrudLoggingAspect(ExternalLogService logService) {
        this.logService = logService;
    }

    @AfterReturning("execution(* naumen.java.project.controller.CountryController.create(..)) || " +
            "execution(* naumen.java.project.controller.CountryController.update(..)) || " +
            "execution(* naumen.java.project.controller.CountryController.delete(..)) || " +
            "execution(* naumen.java.project.controller.CountryController.getAll(..)) || " +
            "execution(* naumen.java.project.controller.CountryController.getById(..))")
    public void afterCrud(JoinPoint jp) {
        String method = jp.getSignature().getName();
        logService.log("Был вызван метод: " + method);
    }
}
