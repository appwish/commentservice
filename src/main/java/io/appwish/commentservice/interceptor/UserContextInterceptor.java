package io.appwish.commentservice.interceptor;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * Intercepts gRPC requests and puts userId into current context.
 */
public class UserContextInterceptor implements ServerInterceptor {

  public static final String USER_ID = "userId";
  public static final Context.Key<String> USER_CONTEXT = Context.key(USER_ID);

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      final ServerCall<ReqT, RespT> call,
      final Metadata headers,
      final ServerCallHandler<ReqT, RespT> next
  ) {
    final String userId = headers.get(Metadata.Key.of(USER_ID, Metadata.ASCII_STRING_MARSHALLER));
    final Context context = Context.current().withValue(USER_CONTEXT, userId);
    return Contexts.interceptCall(context, call, headers, next);
  }
}
