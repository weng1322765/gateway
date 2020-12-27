package jrx.anydmp.gateway.controller;

import jrx.anytxn.common.data.TxnRespCode;
import jrx.anytxn.common.data.TxnRespResult;
import jrx.anydmp.gateway.servie.IOperateEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/operate")
public class OperateController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IOperateEventService operateEventService;

    @GetMapping("event")
    public Mono<TxnRespResult> operateEvent(@RequestParam Integer id) {
        if (id == null) {
            return Mono.just(new TxnRespResult().getFail(TxnRespCode.NOT_EMPTY.getCode(), "id不能为空"));
        }
        try {
            operateEventService.syncOperateEvent(id);
            operateEventService.updateStatusById(id, 1);
        } catch (Exception e) {
            logger.error("系统异常！", e);
            return Mono.just(new TxnRespResult().getFail(TxnRespCode.ERROR.getCode(), e));
        }
        return Mono.just(new TxnRespResult().getSuccess(null));
    }
}
