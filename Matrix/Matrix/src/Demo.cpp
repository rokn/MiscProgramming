//
// Created by rokner on 1/9/18.
//

#include "Demo.hpp"

Demo::Demo(int width, int height) {
    registerAsHandler(DRAW_HANDLER);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    GLuint ebo;
    glGenVertexArrays(1, &_vao);
    glBindVertexArray(_vao);

    float verts[] = {
            50.f,    40.f, 1.0f, 1.0f, 1.0f, 0.0f,
            120.f,   40.f, 1.0f, 1.0f, 1.0f, 0.0f,
            50.f,    50.f, 1.0f, 1.0f, 1.0f, 1.0f,
            120.f,   50.f, 1.0f, 1.0f, 1.0f, 1.0f,
            50.f,    60.f, 1.0f, 1.0f, 1.0f, 0.0f,
            120.f,   60.f, 1.0f, 1.0f, 1.0f, 0.0f,
    };

    uint indices[] = {
            0, 1, 2,
            1, 2, 3,
            2, 3, 4,
            3, 4, 5
    };

    glm::mat3 projectionMatrix = constructProjectionMatrix(width, height);

    _basicShader = new tge::Shader();
    _basicShader->attach("basic.vert")
                .attach("basic.frag")
                .link()
                .activate();

    auto shaderProgram = _basicShader->get();
    auto posAttrib   = glGetAttribLocation(shaderProgram, "position");
    auto colorAttrib = glGetAttribLocation(shaderProgram, "color");

    glGenBuffers(1, &_vbo);
    glBindBuffer(GL_ARRAY_BUFFER, _vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(verts), verts, GL_STATIC_DRAW);

    glGenBuffers(1, &ebo);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    glEnableVertexAttribArray(posAttrib);
    glVertexAttribPointer(posAttrib,   2, GL_FLOAT, GL_FALSE, 6*sizeof(GL_FLOAT), 0);

    glEnableVertexAttribArray(colorAttrib);
    glVertexAttribPointer(colorAttrib, 4, GL_FLOAT, GL_FALSE, 6*sizeof(GL_FLOAT), (void*)(2*sizeof(GL_FLOAT)));

    GLint projectionUniform = glGetUniformLocation(shaderProgram, "projection");
    glUniformMatrix3fv(projectionUniform, 1, GL_FALSE, glm::value_ptr(projectionMatrix));

    glBindVertexArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glUseProgram(0);
}

void Demo::draw() {
    _basicShader->activate();
    glBindVertexArray(_vao);
//    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    glDrawElements(GL_TRIANGLES, 12, GL_UNSIGNED_INT, (void*)0);
    glBindVertexArray(0);
}

Demo::~Demo() {
    delete _basicShader;
}

glm::mat3 Demo::constructProjectionMatrix(int width, int height) {
    glm::mat3 projection(1.f / (width/2), 0.f, 0.f,
                         0.f, -1.f/ (height/2.f), 0.f,
                         -1.f, 1.f, 1.f);
    return projection;
}
