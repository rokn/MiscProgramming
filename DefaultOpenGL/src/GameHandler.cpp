//
// Created by rokner on 12/23/17.
//

#include "GameHandler.hpp"

GameHandler::GameHandler():
_type(NONE)
{
}

int GameHandler::getType() {
    return _type;
}

void GameHandler::registerAsHandler(HandlerType type) {
    _type |= type;
}

void GameHandler::unregisterAsHandler(HandlerType type) {
    _type &= (~type);
}
