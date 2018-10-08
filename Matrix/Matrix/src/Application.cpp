//
// Created by rokner on 12/23/17.
//

#include "Application.hpp"

Application::Application(const Config &config):
    _clearColor(Color(100, 149, 237)), //Cornflower Blue
    _init(true)
//    _clearColor(sf::Color(50,50,50)) //Cornflower Blue
{
    glfwInit();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, config.glMajorVersion);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, config.glMinorVersion);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
    _window = glfwCreateWindow(config.windowWidth,
                               config.windowHeight,
                               config.appName.c_str(), nullptr, nullptr);

    if (_window == nullptr) {
        fprintf(stderr, "Failed to Create OpenGL Context");
        _init = false;
    } else {
        glfwMakeContextCurrent(_window);
        gladLoadGL();
        fprintf(stderr, "OpenGL %s\n", glGetString(GL_VERSION));
    }

}

int Application::run() {
    if(!_init) {
        return -1;
    }

    _isRunning = true;

    while(!glfwWindowShouldClose(_window) && _isRunning) {
        handleEvents();
        update();
        draw();
    }

    glfwTerminate();
    return EXIT_SUCCESS;
}

void Application::draw(){
    glClearColor(_clearColor.r/255.f, _clearColor.g/255.f, _clearColor.b/255.f, _clearColor.a/255);
    glClear(GL_COLOR_BUFFER_BIT);

    for(auto handler : _drawHandlers) {
        handler->draw();
    }

    // Flip Buffers and Draw
    glfwSwapBuffers(_window);
}

void Application::handleEvents() {
    glfwPollEvents();
    internalEventHandle();
}

void Application::internalEventHandle() {
    if(glfwGetKey(_window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
        glfwSetWindowShouldClose(_window, true);
    }
}

void Application::exit() {
    glfwSetWindowShouldClose(_window, true);
    _isRunning = false;
}

void Application::update() {
//    UpdateInfo info;
//    info.setElapsedTime(elapsed);
//    for(auto handler : _updateHandlers) {
//        handler->update(info);
//    }
}

void Application::registerHandler(GameHandler *handler) {
    int type = handler->getType();

    if(type & EVENT_HANDLER) {
        _eventHandlers.push_back(handler);
    }

    if(type & UPDATE_HANDLER) {
        _updateHandlers.push_back(handler);
    }

    if(type & DRAW_HANDLER) {
        _drawHandlers.push_back(handler);
    }
}
