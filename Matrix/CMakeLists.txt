cmake_minimum_required(VERSION 3.0)
project(Matrix)

set(PROJECT_DIR "Matrix")

option(GLFW_BUILD_DOCS OFF)
option(GLFW_BUILD_EXAMPLES OFF)
option(GLFW_BUILD_TESTS OFF)
add_subdirectory(${PROJECT_DIR}/Vendor/glfw)

#option(ASSIMP_BUILD_ASSIMP_TOOLS OFF)
#option(ASSIMP_BUILD_SAMPLES OFF)
#option(ASSIMP_BUILD_TESTS OFF)
#add_subdirectory(${PROJECT_DIR}/Vendor/assimp)
#
#option(BUILD_BULLET2_DEMOS OFF)
#option(BUILD_CPU_DEMOS OFF)
#option(BUILD_EXTRAS OFF)
#option(BUILD_OPENGL3_DEMOS OFF)
#option(BUILD_UNIT_TESTS OFF)
#add_subdirectory(${PROJECT_DIR}/Vendor/bullet)

if(MSVC)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /W4")
else()
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -Wpedantic -Wno-unused-parameter -std=c++11")
    if(NOT WIN32)
        set(GLAD_LIBRARIES dl)
    endif()
endif()

include_directories(${PROJECT_DIR}/include/
#                    ${PROJECT_DIR}/Vendor/assimp/include/
#                    ${PROJECT_DIR}/Vendor/bullet/src/
                    ${PROJECT_DIR}/Vendor/glad/include/
                    ${PROJECT_DIR}/Vendor/glfw/include/
                    ${PROJECT_DIR}/Vendor/glm/
                    ${PROJECT_DIR}/Vendor/stb/)

file(GLOB VENDORS_SOURCES ${PROJECT_DIR}/Vendor/glad/src/glad.c)
file(GLOB PROJECT_HEADERS ${PROJECT_DIR}/include/*.h
                          ${PROJECT_DIR}/include/*.hpp)
file(GLOB PROJECT_SOURCES ${PROJECT_DIR}/src/*.cpp)
file(GLOB PROJECT_SHADERS ${PROJECT_DIR}/Shaders/*.comp
                          ${PROJECT_DIR}/Shaders/*.frag
                          ${PROJECT_DIR}/Shaders/*.geom
                          ${PROJECT_DIR}/Shaders/*.vert)
file(GLOB PROJECT_CONFIGS CMakeLists.txt
                          Readme.md
                         .gitattributes
                         .gitignore
                         .gitmodules)

source_group("Headers" FILES ${PROJECT_HEADERS})
source_group("Shaders" FILES ${PROJECT_SHADERS})
source_group("Sources" FILES ${PROJECT_SOURCES})
source_group("Vendors" FILES ${VENDORS_SOURCES})

add_definitions(-DGLFW_INCLUDE_NONE
                -DPROJECT_SOURCE_DIR=\"${PROJECT_SOURCE_DIR}\")
add_executable(${PROJECT_NAME} ${PROJECT_SOURCES} ${PROJECT_HEADERS}
                               ${PROJECT_SHADERS} ${PROJECT_CONFIGS}
                               ${VENDORS_SOURCES})
target_link_libraries(${PROJECT_NAME} glfw
                      ${GLFW_LIBRARIES} ${GLAD_LIBRARIES})
#                      assimp BulletDynamics BulletCollision LinearMath)
set_target_properties(${PROJECT_NAME} PROPERTIES
    RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/${PROJECT_NAME})

set (source "${CMAKE_SOURCE_DIR}/${PROJECT_DIR}/Shaders")
set (destination "${PROJECT_BINARY_DIR}/${PROJECT_DIR}/Shaders")
add_custom_command(
        TARGET ${PROJECT_NAME} POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E create_symlink ${source} ${destination}
        DEPENDS ${destination}
        COMMENT "Symbolic link shaders folder from ${source} => ${destination}"
)

