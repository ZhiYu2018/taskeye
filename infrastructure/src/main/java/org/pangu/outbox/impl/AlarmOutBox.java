package org.pangu.outbox.impl;

import feign.Headers;
import feign.RequestLine;
import org.pangu.vo.AlarmEvent;

import java.util.List;

public interface AlarmOutBox {
    @RequestLine("POST /alarm/submit")
    @Headers("Content-Type: application/json")
    String submitEvent(AlarmEvent event);
    @RequestLine("POST /alarm/submit")
    @Headers("Content-Type: application/json")
    String batchEvent(List<AlarmEvent> events);
}
