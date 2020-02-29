package org.pangu.outbox;

import org.pangu.vo.AlarmEvent;

import java.util.List;

public interface AlarmNotify {
    void notify(AlarmEvent event);
    void batch(List<AlarmEvent> events);
}
