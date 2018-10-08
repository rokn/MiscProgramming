//
// Created by rokner on 12/23/17.
//

#ifndef FGL_GAMEHANDLER_H
#define FGL_GAMEHANDLER_H

#include <SFML/Graphics.hpp>
#include "UpdateInfo.h"

enum HandlerType {
    NONE          = 0,
    EVENT_HANDLER  = 1,
    UPDATE_HANDLER = 1 << 1,
    DRAW_HANDLER   = 1 << 2,
};

class GameHandler {
public:
    GameHandler();
    virtual bool handle_event(const sf::Event& event){ return false; };
    virtual void update(const UpdateInfo& info){};
    virtual void draw(sf::RenderTarget& surface){};
    int getType();
    void registerAsHandler(HandlerType type);
    void unregisterAsHandler(HandlerType type);
private:
    int _type;
};


#endif //FGL_GAMEHANDLER_H
