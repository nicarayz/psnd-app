package kh.com.psnd.network.task;


import core.lib.network.task.BaseTask;
import kh.com.psnd.helper.TaskHelper;
import kh.com.psnd.network.adapter.PsndService;
import kh.com.psnd.network.request.RequestStaff;
import kh.com.psnd.network.response.ResponseStaff;
import lombok.val;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskStaff extends BaseTask<RequestStaff, Response, ResponseStaff> {

    public TaskStaff(RequestStaff requestStaff) {
        super(TaskHelper.getHttpHeader());
        setData(requestStaff);
    }

    @Override
    protected Response onExecute(Retrofit retrofit, RequestStaff param) throws Exception {
        val service = retrofit.create(PsndService.class);
        return service.getStaff(param).execute();
    }
}
