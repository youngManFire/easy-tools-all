package vip.breakpoint.annotation;

import java.lang.annotation.*;

/**
 * parameters desc
 *
 * @author breakpoint/赵先生
 * 2020/09/23
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MParam {

    String value(); // 作用

    String example() default ""; // 示例
}
