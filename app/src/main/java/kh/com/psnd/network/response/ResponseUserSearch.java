package kh.com.psnd.network.response;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import core.lib.network.response.BaseResponse;
import kh.com.psnd.network.model.UserSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResponseUserSearch extends BaseResponse {

    @SerializedName("result")
    private List<UserSearch> result;

}