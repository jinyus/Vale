#ifndef _UTILS_CALL_H_
#define _UTILS_CALL_H_

#include <globalstate.h>

LLVMValueRef buildSimpleCall(
    LLVMBuilderRef builder,
    LLVMValueRef function,
    LLVMTypeRef funcLT,
    std::vector<LLVMValueRef> args,
    const std::string& name = "");

#endif
