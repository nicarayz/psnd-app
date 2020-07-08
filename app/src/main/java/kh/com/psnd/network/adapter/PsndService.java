package kh.com.psnd.network.adapter;


import kh.com.psnd.network.request.RequestDepartmentType_label_2;
import kh.com.psnd.network.request.RequestDepartment_label_3;
import kh.com.psnd.network.request.RequestGeneralComm_label_1;
import kh.com.psnd.network.request.RequestLogin;
import kh.com.psnd.network.request.RequestOfficeType_label_4;
import kh.com.psnd.network.request.RequestOfficeName_label_5;
import kh.com.psnd.network.request.RequestSearch;
import kh.com.psnd.network.request.RequestSectorName_label_7;
import kh.com.psnd.network.request.RequestSectorType_label_6;
import kh.com.psnd.network.request.RequestStaff;
import kh.com.psnd.network.response.ResponseDepartmentType_Label_2;
import kh.com.psnd.network.response.ResponseDepartment_Label_3;
import kh.com.psnd.network.response.ResponseGeneralComm_Label_1;
import kh.com.psnd.network.response.ResponseLogin;
import kh.com.psnd.network.response.ResponseOfficeName_Label_5;
import kh.com.psnd.network.response.ResponseOfficeType_Label_4;
import kh.com.psnd.network.response.ResponseSearch;
import kh.com.psnd.network.response.ResponseSectionName_Label_7;
import kh.com.psnd.network.response.ResponseSectorType_Label_6;
import kh.com.psnd.network.response.ResponseStaff;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/general_comm
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/dep_type_from_departments
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/departments
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/office_type_id
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/offices_name
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/sec_type_id
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/sectors_name
 * <p>
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/search_staffs
 * POST - https://246chazd1d.execute-api.ap-southeast-1.amazonaws.com/dev/get_staff
 */
public interface PsndService {

    @POST("search_staffs")
    Call<ResponseSearch> search(@Body RequestSearch requestSearch);

    @POST("login")
    Call<ResponseLogin> login(@Body RequestLogin requestLogin);

    @POST("get_staff")
    Call<ResponseStaff> getStaff(@Body RequestStaff requestStaff);

    @POST("general_comm")
    Call<ResponseGeneralComm_Label_1> generalComm_label_1(@Body RequestGeneralComm_label_1 param);

    @POST("dep_type_from_departments")
    Call<ResponseDepartmentType_Label_2> departmentType_label_2(@Body RequestDepartmentType_label_2 param);

    @POST("departments")
    Call<ResponseDepartment_Label_3> department_label_3(@Body RequestDepartment_label_3 param);

    @POST("office_type_id")
    Call<ResponseOfficeType_Label_4> officeType_label_4(@Body RequestOfficeType_label_4 param);

    @POST("offices_name")
    Call<ResponseOfficeName_Label_5> officesName_label_5(@Body RequestOfficeName_label_5 param);

    @POST("sec_type_id")
    Call<ResponseSectorType_Label_6> sectorType_label_6(@Body RequestSectorType_label_6 param);

    @POST("sectors_name")
    Call<ResponseSectionName_Label_7> sectionName_label_7(@Body RequestSectorName_label_7 param);

}
