/**
 * Created by rokner on 5/14/17.
 */
'use strict';

class TargetController{
    constructor (updater, start_target) {
        this.updater = updater;
        if (start_target) {
            this.target = start_target;
        } else {
            this.target = new Point(0, 0);
        }
        this.main_target = this.target
    }

    update (event) {
        this.updater(this, event);
    };

    get target () {
        return this._target;
    };

    set target (target) {
        this._target = target;
    };

    get main_target () {
        return this._main_target;
    };

    set main_target (target) {
        this._main_target = target;
    };
}

class CircleTargetController extends TargetController{
    constructor (radius, speed, offset, wobble, start_target){
        super(function (tc, event) {
            tc.target = this._update_target(event);
        }, start_target);
        this.offset = new Point(radius, 0);
        this.offset.angle = offset;
        this.speed = speed;
        this.wobble = wobble;
        this.start_offset = offset;
        this.exploded = false;
        this.update_radius(radius)
    }

    _update_target(event) {
        if(this.exploded) {
            this._decay(event);
        } else {
        }
        this.radius = this.current_radius + Math.sin(toRadians(event.time*350)) * this.wobble;
        this.offset.angle += this.speed*event.delta;
        return this.main_target.add(this.offset);
    }

    update_radius(new_radius) {
        this.start_radius = new_radius;
        this.current_radius = new_radius;
        this.radius = new_radius;
    }

    set radius (radius) {
        this.offset.length = radius;
    }

    explode (radius, recharge_time) {
        this.exploded = true;
        this.radius = radius;
        this.exploded_radius = radius;
        this.total_recharge = recharge_time;
        this.recharge_left = recharge_time;
    }

    _decay(event) {
        this.recharge_left -= event.delta;
        this.current_radius = lerp(this.start_radius, this.exploded_radius, this.recharge_left / this.total_recharge);
        if(this.recharge_left <= 0) {
            this.exploded = false;
            this.current_radius = this.start_radius;
        }
    }
}
