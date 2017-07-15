#!/usr/bin/python

import sys
import getopt
import string
from itertools import combinations, product


def showHelp():
    print("Usage: passgen -f [FORMAT] --min=1 --max=100")
    print("FORMAT: # - digit; $ - letter; ^ - uppercase letter;\n* - letter or digit; ? - anything; a - a on correct place(escape special symbols\n(ex. /#abc => #abc, #abc => 1abc, 2abc...9abc)")
    sys.exit(2)


def getSymbolCharacters(symbol):
    result = ""
    if symbol == "#":
        result = string.digits
    elif symbol == "$":
        result = string.ascii_lowercase
    elif symbol == "^":
        result = string.ascii_uppercase
    elif symbol == "*":
        result = string.ascii_letters + string.digits
    elif symbol == "?":
        result = string.digits + string.ascii_letters + ",./;'\\][=-`<>?:\"|}{+_)(*&^%$#@!~"
    return result


def genWithoutFormat(minLength, maxLength):
    for i in range(minLength, maxLength+1):
        genWithFormat("?" * i)


def genWithFormat(passFormat):
    substituionList = []
    result = ""
    i = 0
    while i < len(passFormat):
        result = getSymbolCharacters(passFormat[i])
        if passFormat[i] == '/':
            i += 1
            substituionList.append(passFormat[i])
        elif result != "":
            substituionList.append(result)
        else:
            substituionList.append(passFormat[i])
        i += 1
    for sublist in combinations(substituionList[1:], len(substituionList)-1):
        basis = [substituionList[0]] + list(sublist)
        for combo in product(*basis):
            print("".join(combo))


def main(argv):
    passFormat = ""
    minLength = 1
    maxLength = 1
    hasFormat = False

    try:
        opts, args = getopt.getopt(argv, "hf:", ["min=", "max="])
    except getopt.GetoptError:
        showHelp()
    for opt, arg in opts:
        if opt == "-h":
            showHelp()
        elif opt == "-f":
            passFormat = arg
            hasFormat = True
            break
        elif opt == "--min":
            minLength = int(float(arg))
        elif opt == "--max":
            maxLength = int(float(arg))
    if hasFormat:
        genWithFormat(passFormat)
    else:
        genWithoutFormat(minLength, maxLength)


if __name__ == "__main__":
    main(sys.argv[1:])
