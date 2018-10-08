//
// Created by rokner on 12/23/17.
//

#ifndef APPLICATION_H
#define APPLICATION_H

#include "app.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>

#include <memory>
#include <vector>
#include "Config.hpp"
#include "GameHandler.hpp"
#include "Color.hpp"

class Application {
public:
    explicit Application(const Config& config);
    int run();

    void registerHandler(GameHandler* handler);

private:
    GLFWwindow* _window;
    bool _isRunning;
    Color _clearColor;

    std::vector<GameHandler*> _eventHandlers;
    std::vector<GameHandler*> _updateHandlers;
    std::vector<GameHandler*> _drawHandlers;

    void internalEventHandle();
    void exit();

    void handleEvents();

    void update();

    void draw();

    bool _init;
};


#endif //APPLICATION_H
