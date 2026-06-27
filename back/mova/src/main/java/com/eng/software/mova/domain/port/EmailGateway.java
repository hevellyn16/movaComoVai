package com.eng.software.mova.domain.port;

import java.util.Map;

public interface EmailGateway {
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables);
}
