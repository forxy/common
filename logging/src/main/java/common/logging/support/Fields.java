package common.logging.support;

/**
 * Fields to log
 */
public enum Fields {
    ProductName,      //name of the product
    ActivityGUID,     //thread-bound ActivityGUID

    ActivityName,     //name of the activity, e.g. "endpoint-v1"
    ActivityStep,     //step of the activity e.g. rq or rs
    OperationName,    //name of the operation  e.g. "SomeRequestRQ"

    HostLocal,        //ip of a local answering host
    HostRemote,       //ip of a remote calling host

    Timestamp,
    TimestampStart,
    TimestampEnd,
    Duration,         //duration in ms, derived from System.currentTimeMillis()
    DurationNano,     //duration in ms, derived from  System.nanoTime()
    StatusCode,       //status code

    RequestURL,       //http request url
    RequestMethod,    //http request method
    RequestHeaders,   //http request headers

    RequestPayload,

    ResponseStatus,   //http response status code
    ResponseURL,      //http response redirect location
    ResponseHeaders,  //http response headers

    ResponsePayload;  //response payload

    public enum ActivitySteps {
        rq,
        rs
    }
}