package jrx.anydmp.gateway.servie;

public interface IOperateEventService {

    String syncOperateEvent(Integer id) throws Exception;

    void updateStatusById(Integer id, Integer status);
}
