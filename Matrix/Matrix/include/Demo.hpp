//
// Created by rokner on 1/9/18.
//

#ifndef MATRIX_DEMO_H
#define MATRIX_DEMO_H


#include "GameHandler.hpp"
#include "Shader.hpp"

class Demo : public GameHandler {
public:
    Demo(int width, int height);
    virtual ~Demo();
    void draw() override;
private:
    tge::Shader* _basicShader;
    GLuint _vao;
    GLuint _vbo;

    glm::mat3 constructProjectionMatrix(int width, int height);

};


#endif //MATRIX_DEMO_H
