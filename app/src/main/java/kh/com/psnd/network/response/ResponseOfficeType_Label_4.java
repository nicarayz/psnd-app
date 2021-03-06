package kh.com.psnd.network.response;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import core.lib.network.response.BaseResponse;
import kh.com.psnd.network.model.OfficeType_label_4;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResponseOfficeType_Label_4 extends BaseResponse {

    @SerializedName("result")
    private List<OfficeType_label_4> result = new ArrayList<>();

    public OfficeType_label_4[] getResultArrays() {
        val arrays = new OfficeType_label_4[result.size()];
        for (val item : result) {
            arrays[result.indexOf(item)] = item;
        }
        return arrays;
    }
}
