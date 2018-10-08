//
// Created by rokner on 12/23/17.
//

#ifndef FGL_APPLICATION_H
#define FGL_APPLICATION_H

#include <SFML/Graphics.hpp>
#include <memory>
#include "Config.h"
#include "GameHandler.h"

class Application {
public:
    Application(const Config& config);
    int run();

    void registerHandler(GameHandler* handler);

private:
    std::shared_ptr<sf::RenderWindow> _renderWindow;
    bool _isRunning;
    sf::Color _clearColor;

    std::vector<GameHandler*> _eventHandlers;
    std::vector<GameHandler*> _updateHandlers;
    std::vector<GameHandler*> _drawHandlers;

    void internalEventHandle(const sf::Event &event);
    void exit();

    void handleEvents();

    void update(const sf::Time &elapsed);

    void draw();
};


#endif //FGL_APPLICATION_H
