package kh.com.psnd.network.response;


import core.lib.network.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResponseUserCheck extends BaseResponse {
    public boolean isFound() {
        return getStatusCode().equals("200");
    }
}
