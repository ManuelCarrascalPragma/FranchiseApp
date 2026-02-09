package co.com.nequi.api.handler;

import co.com.nequi.api.models.ErrorResponse;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@Slf4j
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    private static final String INVALID_ID_FORMAT = "The provided ID must be a valid number";
    private static final String ROUTE_NOT_FOUND = "The requested route does not exist";
    private static final String INVALID_INPUT_FORMAT = "Error in input data format";
    private static final String UNEXPECTED_ERROR = "An unexpected error occurred in the system";

    public GlobalErrorHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                              ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
        this.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);

        HttpStatus status;
        String message;

        if (error instanceof BusinessException) {
            status = HttpStatus.BAD_REQUEST;
            message = error.getMessage();
        } else if (error instanceof ResourceNotFoundException){
            status = HttpStatus.NOT_FOUND;
            message = error.getMessage();
        } else if (error instanceof NumberFormatException) {
            status = HttpStatus.BAD_REQUEST;
            message = INVALID_ID_FORMAT;
        } else if (error instanceof NoResourceFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = ROUTE_NOT_FOUND;
        } else if (error instanceof ServerWebInputException) {
            status = HttpStatus.BAD_REQUEST;
            message = INVALID_INPUT_FORMAT;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = UNEXPECTED_ERROR;
            log.error("Unexpected system error", error);
        }

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new ErrorResponse(message, status.value(), status.getReasonPhrase())));
    }
}
