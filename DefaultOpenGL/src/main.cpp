#include <FloatingNetwork.h>
#include <initium_novum/World.h>
#include "Application.h"

int main() {
    srand(time(NULL));
    Config config;
    config.windowWidth = 1000;
    config.windowHeight = 800;
    config.appName = "FGL";
    config.antialiasLevel = 4;

    auto *application = new Application(config);

    sf::Rect<int> area(0, 0, config.windowWidth, config.windowHeight);
    auto *network = new FloatingNetwork(area, 100, 150);

    application->registerHandler(network);

//    auto *world = new in::World(area);
//
//    application->registerHandler(world);

    int exitCode = application->run();

//    delete world;
    delete network;
    delete application;

    return exitCode;
}
