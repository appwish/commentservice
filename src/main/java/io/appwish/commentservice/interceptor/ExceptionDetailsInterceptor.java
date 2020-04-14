package io.appwish.commentservice.interceptor;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Intercepts exceptions and writes them to gRPC response.
 */
public class ExceptionDetailsInterceptor implements ServerInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(ExceptionDetailsInterceptor.class.getName());

  public ExceptionDetailsInterceptor() {
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, final Metadata headers,
      final ServerCallHandler<ReqT, RespT> next) {
    final ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
      @Override
      public void close(Status status, final Metadata trailers) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Interceptor: " + (status.getCause() == null ? "null" : status.getCause().getClass().getName()));
        }
        if (status.getCode() == Status.Code.UNKNOWN && status.getDescription() == null && status.getCause() != null) {
          final Throwable e = status.getCause();
          status = Status.INTERNAL.withDescription(e.getMessage()).augmentDescription(stacktraceToString(e));
        }
        super.close(status, trailers);
      }
    };

    return next.startCall(wrappedCall, headers);
  }

  private String stacktraceToString(final Throwable e) {
    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
