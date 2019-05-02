package object;

public enum ReqType {
    JOIN, SEND, LOOKUP, UPLOAD, FILE_REQ, FILE_RESP,
    FILE_ACK, STABILIZE_PRED_RESP, STABILIZE_PRED_REQ, 
    STABILIZE_PRED_SET, SUCCESSOR_REQ,
    SUCCESSOR_RESP, TRANSFER, SETUP, SETUP_RESP, AFFECTED_JOIN
}