package vn.ontaxi.hub.component.abstracts;

import org.springframework.context.MessageSource;

abstract class AbstractComponent {
    protected MessageSource messageSource;

    AbstractComponent(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
