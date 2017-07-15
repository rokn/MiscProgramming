// i = 50;
// inc = 1;
// var start = new Point(100 + i, 100);
// var size = new Size(100, 50);
// var rect = new Rectangle(start, size);
// var path = new Path.Rectangle(rect);
// path.fillColor = '#f00';
// path.strokeColor = '#000'
//
// var myPath = new Path();
// myPath.strokeColor = 'black';

// This function is called whenever the user
// clicks the mouse in the view:
// function onMouseDown(event) {
//     // Add a segment to the path at the position of the mouse:
//     console.log("baka");
//     myPath.add(event.point);
// }

paper.install(window);

window.onload = function() {

    paper.setup('mainCanvas');
    let speed = 1000;
    let circle = new Path.Circle({
        center: view.center,
        radius: 10,
        strokeColor: 'black',
        fillColor: 'white',
    });
    let binded = false;
    let radius = 30;
    let rotateSpeed = 150;
    let wobble = 15;
    let binded_to;
    // let is_active = true;

    let worker = new Worker(circle, speed, view.center);
    worker.set_target_controller(new CircleTargetController(radius, rotateSpeed, 0, wobble, view.center));
    let workers = [worker];

    for(i = 1; i < 6; i++){
        worker = new Worker(circle.clone(), speed, view.center);
        worker.set_target_controller(new CircleTargetController(radius, rotateSpeed, i*60, wobble, view.center));
        workers.push(worker)
    }

    let pos = view.center;

    view.onMouseDown = function (event) {
        for (worker of workers){
            worker.get_target_controller().explode(500,0.5);
        }
    };

    view.onMouseMove =function (event) {
        pos = event.point;
        if(!binded) {
            for (worker of workers) {
                worker.get_target_controller().main_target = pos;
            }
        };
        // view.draw();
    };

    view.onFrame = function (event) {
        // if(!is_active) return;

        for (worker of workers){
            worker.update(event);
        }

        // direction = pos - [50, 0] - myCircle2.position;
        // distance = direction.length;
        // direction.normalize();
        // myCircle2.position += direction * Math.min(distance, speed) * event.delta;
        // console.log(pos)
    };

    make_selectable('.my-selectable');

    function make_selectable(elements) {
        $(elements).mouseenter(function(e){
            binded = true;
            binded_to = $(this);
            attach_to(binded_to);

        });

        $(elements).click(function(e) {
            for (worker of workers){
                worker.get_target_controller().explode(500,0.5);
            }
        });

        $(elements).mouseleave(function(e){
            for (worker of workers){
                let control = worker.get_target_controller();
                control.update_radius(radius);
            }
            binded = false;
        });
    }

    function attach_to(element) {
        let width = element.width();
        let height = element.height();
        let rect = element[0].getBoundingClientRect();

        let centerX = rect.left + width / 2;
        let centerY = rect.top + height / 2;
        for (worker of workers){
            let control = worker.get_target_controller();
            control.main_target = new Point(centerX, centerY);
            control.update_radius(Math.sqrt(width*width+height*height));
        }
    }

    $(window).scroll(function() {
        if(binded) {
            attach_to(binded_to);
        }
    });

    // $(window).focus(function(){
    //     is_active = true;
    //     console.log(is_active);
    // });
    //
    // $(window).blur(function(){
    //     is_active = false;
    //     console.log(is_active);
    // });
};

