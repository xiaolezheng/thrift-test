namespace java com.lxz.thrift.test1

enum OperationType {
  ADD,
  MUL,
  DIV,
  SUB
}

enum ArgType {
  INT,
  LONG,
}

struct Request {
  1: required i32 id;
  2: required i32 arg1;
  3: required i32 arg2;
  4: required ArgType argType;
  5: required OperationType operationType;
}

struct Response {
  1: i32 id;
  2: i32 result;
  3: ArgType resType;
}

service CalculatorService {
  Response invoke(1: Request req);
  oneway void ping();
}