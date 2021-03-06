package kh.com.psnd.network.task;


import core.lib.network.task.BaseTask;
import kh.com.psnd.helper.TaskHelper;
import kh.com.psnd.network.adapter.PsndService;
import kh.com.psnd.network.request.RequestConfig;
import kh.com.psnd.network.response.ResponseConfig;
import lombok.val;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskConfig extends BaseTask<RequestConfig, Response, ResponseConfig> {

    public TaskConfig() {
        super(TaskHelper.getHttpHeader());
        setData(new RequestConfig());
    }

    @Override
    protected Response onExecute(Retrofit retrofit, RequestConfig param) throws Exception {
        val service = retrofit.create(PsndService.class);
        return service.config(param).execute();
    }
}
