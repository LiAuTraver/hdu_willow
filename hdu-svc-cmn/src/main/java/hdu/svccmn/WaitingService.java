package hdu.svccmn;

import com.hdu.hdufpga.entity.Result;

public interface WaitingService {
    UserConnectionVO userInQueue(String token) throws Exception;

    Result checkAvailability(String token) throws Exception;

    boolean freezeConnection(String token);
}
