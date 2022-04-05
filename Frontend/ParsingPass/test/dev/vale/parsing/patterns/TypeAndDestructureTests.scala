package dev.vale.parsing.patterns

import dev.vale.Collector
import dev.vale.parsing.{PatternParser, TestParseUtils}
import dev.vale.parsing.ast.{CallPT, DestructureP, IgnoredLocalNameDeclarationP, LocalNameDeclarationP, NameOrRunePT, NameP, PatternPP, TuplePT}
import dev.vale.parsing.ast.Patterns._
import dev.vale.parsing._
import dev.vale.parsing.ast.IgnoredLocalNameDeclarationP
import dev.vale.Collector
import org.scalatest.{FunSuite, Matchers}

class TypeAndDestructureTests extends FunSuite with Matchers with Collector with TestParseUtils {
  private def compile[T](code: String): PatternPP = {
    compile(new PatternParser().parsePattern(_), code)
  }

  test("Empty destructure") {
    compile("_ Muta[]") shouldHave {
      case PatternPP(_,_,
          Some(IgnoredLocalNameDeclarationP(_)),
          Some(NameOrRunePT(NameP(_, "Muta"))),
          Some(DestructureP(_,Vector())),
          None) =>
    }
  }

  test("Templated destructure") {
    compile("_ Muta<int>[]") shouldHave {
      case PatternPP(_,_,
          Some(IgnoredLocalNameDeclarationP(_)),
          Some(
            CallPT(_,
              NameOrRunePT(NameP(_, "Muta")),
              Vector(NameOrRunePT(NameP(_, "int"))))),
          Some(DestructureP(_,Vector())),
          None) =>
    }
    compile("_ Muta<R>[]") shouldHave {
        case PatternPP(_,_,
        Some(IgnoredLocalNameDeclarationP(_)),
          Some(
            CallPT(_,
              NameOrRunePT(NameP(_, "Muta")),
              Vector(NameOrRunePT(NameP(_, "R"))))),
          Some(DestructureP(_,Vector())),
          None) =>
    }
  }


  test("Destructure with type outside") {
    compile("_ (int, bool)[a, b]") shouldHave {
      case PatternPP(_,_,
          Some(IgnoredLocalNameDeclarationP(_)),
          Some(
            TuplePT(_,
                Vector(
                  NameOrRunePT(NameP(_, "int")),
                  NameOrRunePT(NameP(_, "bool"))))),
          Some(DestructureP(_,Vector(capture("a"), capture("b")))),
          None) =>
    }
  }
  test("Destructure with typeless capture") {
    compile("_ Muta[b]") shouldHave {
      case PatternPP(_,_,
          Some(IgnoredLocalNameDeclarationP(_)),
          Some(NameOrRunePT(NameP(_, "Muta"))),
          Some(DestructureP(_,Vector(PatternPP(_,_,Some(LocalNameDeclarationP(NameP(_, "b"))),None,None,None)))),
          None) =>
    }
  }
  test("Destructure with typed capture") {
    compile("_ Muta[b Marine]") shouldHave {
      case PatternPP(_,_,
          Some(IgnoredLocalNameDeclarationP(_)),
          Some(NameOrRunePT(NameP(_, "Muta"))),
          Some(DestructureP(_,Vector(PatternPP(_,_,Some(LocalNameDeclarationP(NameP(_, "b"))),Some(NameOrRunePT(NameP(_, "Marine"))),None,None)))),
          None) =>
    }
  }
  test("Destructure with unnamed capture") {
    compile("_ Muta[_ Marine]") shouldHave {
      case PatternPP(_,_,
          Some(IgnoredLocalNameDeclarationP(_)),
          Some(NameOrRunePT(NameP(_, "Muta"))),
          Some(DestructureP(_,Vector(PatternPP(_,_,Some(IgnoredLocalNameDeclarationP(_)),Some(NameOrRunePT(NameP(_, "Marine"))),None,None)))),
          None) =>
    }
  }
  test("Destructure with runed capture") {
    compile("_ Muta[_ R]") shouldHave {
      case PatternPP(_,_,
          Some(IgnoredLocalNameDeclarationP(_)),
          Some(NameOrRunePT(NameP(_, "Muta"))),
          Some(DestructureP(_,Vector(PatternPP(_,_,Some(IgnoredLocalNameDeclarationP(_)),Some(NameOrRunePT(NameP(_, "R"))),None,None)))),
          None) =>
        }
  }
}