/**
 * Created by rokner on 5/14/17.
 */

function Worker(shape, speed, start_position) {
    this.position = start_position;
    this.target = start_position;
    this.shape = shape;
    this.speed = speed;

    this.update = function(event) {
        if(this.target_controller) {
            this.target_controller.update(event);
            this.target = this.target_controller.target;
        }

        var direction = this.target.subtract(this.position);
        var distance = direction.length;
        direction = direction.normalize();
        var smooth_speed = Math.min(Math.pow(distance,1.4), this.speed);
        this.position = this.position.add(direction.multiply(smooth_speed * event.delta));
        this._refresh_shape();
    };

    this.set_target = function (target) {
        this.target = target;
    };

    this.set_target_controller = function(controller) {
        this.target_controller = controller;
    };

    this.get_target_controller = function() {
        return this.target_controller;
    };

    this._refresh_shape = function () {
        this.shape.position = this.position;
    };

    this._refresh_shape();
}

