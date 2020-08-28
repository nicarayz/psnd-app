package kh.com.psnd.network.task;


import core.lib.network.base.BaseNetwork;
import core.lib.network.base.HttpHeader;
import core.lib.network.task.BaseTask;
import kh.com.psnd.helper.LoginManager;
import kh.com.psnd.network.adapter.PsndService;
import kh.com.psnd.network.request.RequestUserUpdateRole;
import kh.com.psnd.network.response.ResponseUserUpdateRole;
import lombok.val;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskUserUpdateRole extends BaseTask<RequestUserUpdateRole, Response, ResponseUserUpdateRole> {

    public TaskUserUpdateRole(RequestUserUpdateRole requestUserUpdateRole) {
        super(new HttpHeader(BaseNetwork.getToken(), LoginManager.getUserToken()));
        setData(requestUserUpdateRole);
    }

    @Override
    protected Response onExecute(Retrofit retrofit, RequestUserUpdateRole param) throws Exception {
        val service = retrofit.create(PsndService.class);
        return service.userUpdateRole(param).execute();
    }
}
