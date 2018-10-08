//
// Created by rokner on 12/23/17.
//

#ifndef FGL_UPDATEINFO_H
#define FGL_UPDATEINFO_H


#include <SFML/System.hpp>

class UpdateInfo {
public:
    const sf::Time& getElapsedTime() const;
    float elapsedSeconds() const;
    void setElapsedTime(const sf::Time &deltaSeconds);

private:
    sf::Time elapsed;
};


#endif //FGL_UPDATEINFO_H
