#include "Demo.hpp"
#include "Application.hpp"

int main() {
    srand(time(0));
    Config config;
    config.windowWidth = 1000;
    config.windowHeight = 800;
    config.appName = "Matrix";
    config.antialiasLevel = 4;
    config.glMajorVersion = 3;
    config.glMinorVersion = 3;

    auto *application = new Application(config);

    auto *demo = new Demo(config.windowWidth, config.windowHeight);

    application->registerHandler(demo);

    int exitCode = application->run();

    delete demo;
    delete application;

    return exitCode;
}
