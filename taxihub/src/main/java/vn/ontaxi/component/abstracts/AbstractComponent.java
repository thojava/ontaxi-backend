package vn.ontaxi.component.abstracts;

import org.springframework.context.MessageSource;

abstract class AbstractComponent {
    protected final MessageSource messageSource;

    AbstractComponent(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
