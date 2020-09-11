
#include <region/common/defaultimmutables/defaultimmutables.h>
#include "structsrouter.h"

ReferendStructsRouter::ReferendStructsRouter(
    GetReferendStructsSource referendStructsRouter_,
    GetWeakRefStructsSource weakRefStructsRouter_)
    : getReferendStructsSource(referendStructsRouter_),
      weakRefStructsRouter(weakRefStructsRouter_) {}

ControlBlock* ReferendStructsRouter::getControlBlock(Referend* referend) {
  return &getReferendStructsSource(referend)->getControlBlock(referend);
}
LLVMTypeRef ReferendStructsRouter::getInnerStruct(StructReferend* structReferend) {
  return getReferendStructsSource(structReferend)->getInnerStruct(structReferend);
}
LLVMTypeRef ReferendStructsRouter::getWrapperStruct(StructReferend* structReferend) {
  return getReferendStructsSource(structReferend)->getWrapperStruct(structReferend);
}
LLVMTypeRef ReferendStructsRouter::getKnownSizeArrayWrapperStruct(KnownSizeArrayT* ksaMT) {
  return getReferendStructsSource(ksaMT)->getKnownSizeArrayWrapperStruct(ksaMT);
}
LLVMTypeRef ReferendStructsRouter::getUnknownSizeArrayWrapperStruct(UnknownSizeArrayT* usaMT) {
  return getReferendStructsSource(usaMT)->getUnknownSizeArrayWrapperStruct(usaMT);
}
LLVMTypeRef ReferendStructsRouter::getInterfaceRefStruct(InterfaceReferend* interfaceReferend) {
  return getReferendStructsSource(interfaceReferend)->getInterfaceRefStruct(interfaceReferend);
}
LLVMTypeRef ReferendStructsRouter::getInterfaceTableStruct(InterfaceReferend* interfaceReferend) {
  return getReferendStructsSource(interfaceReferend)->getInterfaceTableStruct(interfaceReferend);
}
void ReferendStructsRouter::translateStruct(StructReferend* structReferend, std::vector<LLVMTypeRef> membersLT) {
  return getReferendStructsSource(structReferend)->translateStruct(structReferend, membersLT);
}
void ReferendStructsRouter::declareStruct(StructDefinition* structM) {
  return getReferendStructsSource(structM)->declareStruct(structM);
}
void ReferendStructsRouter::declareEdge(Edge* edge) {
  return getReferendStructsSource(edge->structName)->declareEdge(edge);
}
void ReferendStructsRouter::translateEdge(Edge* edge, std::vector<LLVMValueRef> functions) {
  return getReferendStructsSource(edge->structName)->translateEdge(edge, functions);
}
void ReferendStructsRouter::declareInterface(InterfaceReferend* name) {
  return getReferendStructsSource(name)->declareInterface(name);
}
void ReferendStructsRouter::translateInterface(InterfaceReferend* interfaceReferend, std::vector<LLVMTypeRef> interfaceMethodTypesL) {
  return getReferendStructsSource(interfaceReferend)->translateInterface(interfaceReferend, interfaceMethodTypesL);
}
void ReferendStructsRouter::declareKnownSizeArray(KnownSizeArrayT* knownSizeArrayMT) {
  return getReferendStructsSource(knownSizeArrayMT)->declareKnownSizeArray(knownSizeArrayMT);
}
void ReferendStructsRouter::declareUnknownSizeArray(UnknownSizeArrayT* unknownSizeArrayMT) {
  return getReferendStructsSource(unknownSizeArrayMT)->declareUnknownSizeArray(unknownSizeArrayMT);
}
void ReferendStructsRouter::translateUnknownSizeArray(UnknownSizeArrayT* unknownSizeArrayMT, LLVMTypeRef elementLT) {
  return getReferendStructsSource(unknownSizeArrayMT)->translateUnknownSizeArray(unknownSizeArrayMT, elementLT);
}
void ReferendStructsRouter::translateKnownSizeArray(KnownSizeArrayT* knownSizeArrayMT, LLVMTypeRef elementLT) {
  return getReferendStructsSource(knownSizeArrayMT)->translateKnownSizeArray(knownSizeArrayMT, elementLT);
}

LLVMTypeRef WeakRefStructsRouter::getStructWeakRefStruct(StructReferend* structReferend) {
  return getWeakRefStructsSource(structReferend)->getStructWeakRefStruct(structReferend);
}
LLVMTypeRef WeakRefStructsRouter::getKnownSizeArrayWeakRefStruct(KnownSizeArrayT* ksaMT) {
  return getWeakRefStructsSource(ksaMT)->getKnownSizeArrayWeakRefStruct(ksaMT);
}
LLVMTypeRef WeakRefStructsRouter::getUnknownSizeArrayWeakRefStruct(UnknownSizeArrayT* usaMT) {
  return getWeakRefStructsSource(usaMT)->getUnknownSizeArrayWeakRefStruct(usaMT);
}
LLVMTypeRef WeakRefStructsRouter::getInterfaceWeakRefStruct(InterfaceReferend* interfaceReferend) {
  return getWeakRefStructsSource(interfaceReferend)->getInterfaceWeakRefStruct(interfaceReferend);
}