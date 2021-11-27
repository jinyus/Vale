package net.verdagon.vale.parser.rules

import net.verdagon.vale.parser._

import scala.util.parsing.combinator.RegexParsers

trait RuleParser extends RegexParsers with ParserUtils {

  private[parser] def ruleTemplexPR: Parser[ITemplexPT]

  // Add any new rules to the "Nothing matches empty string" test!

  private[parser] def level0PR: Parser[IRulexPR] = {
    ruleTemplexPR ^^ TemplexPR
  }

  private[parser] def typedPR: Parser[TypedPR] = {
    pos ~ underscoreOr(typeIdentifier) ~ (white ~> typePR) ~ pos ^^ {
      case begin ~ maybeRune ~ tyype ~ end => TypedPR(Range(begin, end), maybeRune, tyype)
    }
  }

  private[parser] def typePR: Parser[ITypePR] = {
    "Ownership" ^^^ OwnershipTypePR |
    "Mutability" ^^^ MutabilityTypePR |
    "Permission" ^^^ PermissionTypePR |
    "Location" ^^^ LocationTypePR |
    "RefList" ^^^ CoordListTypePR |
    "Ref" ^^^ CoordTypePR |
    "Prot" ^^^ PrototypeTypePR |
    // Int must be after Interface, otherwise we'll have a hanging "erface"
    // Same with Kint and KindTemplate
    "int" ^^^ IntTypePR |
    "i64" ^^^ IntTypePR |
    "Kind" ^^^ KindTypePR
  }

  private[parser] def destructurePR: Parser[IRulexPR] = {
    (pos ~
      (typePR <~ "(" <~ optWhite) ~
      repsep(rulePR, optWhite ~> "," <~ optWhite) ~
      pos <~ optWhite <~ ")" ^^ {
      case begin ~ tyype ~ components ~ end => ComponentsPR(Range(begin, end), TypedPR(Range(begin, end), None, tyype), components.toVector)
    }) |
    (pos ~
      (typedPR <~ "(" <~ optWhite) ~
      repsep(rulePR, optWhite ~> "," <~ optWhite) ~
      pos <~ optWhite <~ ")" ^^ {
      case begin ~ container ~ components ~ end => ComponentsPR(Range(begin, end), container, components.toVector)
    })
  }

  private[parser] def dotPR(innerRule: Parser[IRulexPR]): Parser[IRulexPR] = {
    pos ~ (innerRule <~ optWhite <~ "." <~ optWhite) ~ typeIdentifier ~ pos ^^ {
      case begin ~ inner ~ name ~ end => DotPR(Range(begin, end), inner, name)
    }
  }

  private[parser] def orPR(inner: Parser[IRulexPR]): Parser[IRulexPR] = {
    pos ~ (inner <~ optWhite <~ "|" <~ optWhite) ~ rep1sep(inner, optWhite ~> "|" <~ optWhite) ~ pos ^^ {
      case begin ~ firstPossibility ~ restPossibilities ~ end => OrPR(Range(begin, end), Vector(firstPossibility) ++ restPossibilities)
    }
  }

  private[parser] def level1PR: Parser[IRulexPR] = {
    typedPR | level0PR
  }

  private[parser] def level2PR: Parser[IRulexPR] = {
    destructurePR | level1PR
  }

  private[parser] def level3PR: Parser[IRulexPR] = {
    implementsPR |
    refListCompoundMutabilityPR |
    isInterfacePR |
    existsPR |
    dotPR(level2PR) |
    level2PR
  }

  private[parser] def level4PR: Parser[IRulexPR] = {
    orPR(level3PR) | level3PR
  }

  private[parser] def level5PR: Parser[IRulexPR] = {
    equalsPR(level4PR) |
    level4PR
  }

  private[parser] def rulePR: Parser[IRulexPR] = {
    level5PR
  }

  // Add any new rules to the "Nothing matches empty string" test!

  private[parser] def identifyingRegionRuneAttribute: Parser[IRuneAttributeP] = {
    pos ~ "ro" ~ pos ^^ { case begin ~ _ ~ end => ReadOnlyRuneAttributeP(Range(begin, end)) } |
    pos ~ "bump" ~ pos ^^ { case begin ~ _ ~ end => BumpRuneAttributeP(Range(begin, end)) } |
    pos ~ "pool" ~ pos ^^ { case begin ~ _ ~ end => PoolRuneAttributeP(Range(begin, end)) } |
    pos ~ "arena" ~ pos ^^ { case begin ~ _ ~ end => ArenaRuneAttributeP(Range(begin, end)) } |
    pos ~ ("coord" ^^^ CoordTypePR | "kind" ^^^ KindTypePR | "reg" ^^^ RegionTypePR) ~ pos ^^ { case begin ~ tyype ~ end => TypeRuneAttributeP(Range(begin, end), tyype) }
  }

  private[parser] def identifyingRune: Parser[IdentifyingRuneP] = {
    pos ~ opt(pstr("'")) ~ exprIdentifier ~ rep(white ~> identifyingRegionRuneAttribute) ~ pos ^^ {
      case begin ~ maybeIsRegion ~ name ~ regionAttributes ~ end => {
        val isRegionAttrInList =
          maybeIsRegion match {
            case None => Vector.empty
            case Some(NameP(range, _)) => Vector(TypeRuneAttributeP(range, RegionTypePR))
          }
        IdentifyingRuneP(Range(begin, end), name, isRegionAttrInList ++ regionAttributes)
      }
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!

  private[parser] def identifyingRunesPR: Parser[IdentifyingRunesP] = {
    pos ~ ("<" ~> optWhite ~> repsep(identifyingRune, optWhite ~> "," <~ optWhite) <~ optWhite <~ ">") ~ pos ^^ {
      case begin ~ runes ~ end => IdentifyingRunesP(Range(begin, end), runes.toVector)
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!

  def templateRulesPR: Parser[TemplateRulesP] = {
    pos ~ ("rules" ~> optWhite ~> "(" ~> optWhite ~> repsep(rulePR, optWhite ~> "," <~ optWhite) <~ optWhite <~ ")") ~ pos ^^ {
      case begin ~ rules ~ end => TemplateRulesP(Range(begin, end), rules.toVector)
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!

  // Atomic means no neighboring, see parser doc.
  private[parser] def implementsPR: Parser[IRulexPR] = {
    pos ~ pstr("implements") ~ (optWhite ~> "(" ~> optWhite ~> rulePR <~ optWhite <~ "," <~ optWhite) ~
        (rulePR <~ optWhite <~ ")") ~ pos ^^ {
      case begin ~ impl ~ struct ~ interface ~ end => BuiltinCallPR(Range(begin, end), impl, Vector(struct, interface))
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!

  // Atomic means no neighboring, see parser doc.
  private[parser] def refListCompoundMutabilityPR: Parser[IRulexPR] = {
    pos ~ pstr("refListCompoundMutability") ~ (optWhite ~> "(" ~> optWhite ~> (rulePR <~ optWhite <~ ")")) ~ pos ^^ {
      case begin ~ name ~ arg ~ end => BuiltinCallPR(Range(begin, end), name, Vector(arg))
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!

  // Atomic means no neighboring, see parser doc.
  private[parser] def isInterfacePR: Parser[IRulexPR] = {
    pos ~ pstr("isInterface") ~ (optWhite ~> "(" ~> optWhite ~> rulePR <~ optWhite <~ ")") ~ pos ^^ {
      case begin ~ name ~ arg ~ end => BuiltinCallPR(Range(begin, end), name, Vector(arg))
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!

  // Atomic means no neighboring, see parser doc.
  private[parser] def existsPR: Parser[IRulexPR] = {
    pos ~ pstr("exists") ~ (optWhite ~> "(" ~> optWhite ~> rulePR <~ optWhite <~ ")") ~ pos ^^ {
      case begin ~ exists ~ thing ~ end => BuiltinCallPR(Range(begin, end), exists, Vector(thing))
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!

  private[parser] def packPR: Parser[PackPR] = {
    pos ~ ("(" ~> optWhite ~> repsep(rulePR, optWhite ~> "," <~ optWhite) <~ optWhite <~ ")") ~ pos ^^ {
      case begin ~ thing ~ end => PackPR(Range(begin, end), thing.toVector)
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!

  private[parser] def equalsPR(inner: Parser[IRulexPR]): Parser[EqualsPR] = {
    pos ~ (inner <~ optWhite <~ "=" <~ optWhite) ~ inner ~ pos ^^ {
      case begin ~ left ~ right ~ end => EqualsPR(Range(begin, end), left, right)
    }
  }

  // Add any new rules to the "Nothing matches empty string" test!
}
