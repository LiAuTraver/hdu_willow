package hdu.svccmn;

import com.hdu.hdufpga.entity.vo.UserVO;

public interface TokenService {
    String generateToken(UserVO userVO) throws Exception;

    Boolean checkToken(String token) throws Exception;

    UserConnectionVO reload(String token) throws Exception;
}
