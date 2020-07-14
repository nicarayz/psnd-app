package kh.com.psnd.network.model;

import com.google.gson.annotations.SerializedName;

import core.lib.network.model.BaseGson;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Search extends BaseGson {
    public static final String EXTRA          = "Search";
    public static final String EXTRA_LIST     = "Search_List";
    public static final String EXTRA_POSITION = "Position";

    @SerializedName("staffID")
    private int    staffId;
    @SerializedName("staffNumber")
    private String staffNumber;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("department")
    private String department;
    @SerializedName("photo")
    private String photo;
    @SerializedName("general_commissariat")
    private String generalCommissariat;

    public Search() {

    }

    public Search(Staff staff) {
        setStaffId(staff.getStaffId());
        setDepartment(staff.getDepartment());
        setFullName(staff.getFullName());
        setGeneralCommissariat(staff.getGeneralCommissariat());
        setPhoto(staff.getPhoto());
        setStaffNumber(staff.getId());
    }
}
