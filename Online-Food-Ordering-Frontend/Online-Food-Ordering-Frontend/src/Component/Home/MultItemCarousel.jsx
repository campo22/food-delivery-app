import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import Slider from "react-slick";
import { topMeel } from "./topMeel";
import CarouselItem from "./CarouselItem";

const MultItemCarousel = () => {

    const settings = {
        dots: true, // esto agrega los puntos de navegación
        infinite: true, // esto permite que el carrusel se desplace infinitamente
        speed: 500, // velocidad de desplazamiento
        slidesToShow: 5, // número de elementos a mostrar en pantalla
        slidesToScroll: 1, // número de elementos a desplazar al hacer clic en los botones de navegación
        initialSlide: 0, // índice del primer elemento a mostrar
        autoplay: true, // activa el carrusel automático
        autoplaySpeed: 1500, // velocidad de desplazamiento automático en milisegundos
        pauseOnHover: false, // pausa el carrusel al pasar el mouse sobre él
        arrows: false,  // muestra los botones de navegación
        centerMode: true, // centra el elemento actual en la pantalla
        centerPadding: '0px', // espacio alrededor del elemento centrado
        variableWidth: false, // permite que los elementos tengan un ancho variable
        adaptiveHeight: true, // ajusta la altura del carrusel al contenido 
        swipeToSlide: true, // permite deslizar los elementos con el dedo
        draggable: true, // permite arrastrar los elementos con el mouse
        touchThreshold: 10, // umbral de toque para activar el deslizamiento
        swipe: true, // permite deslizar los elementos con el dedo
        responsive: [
            {
                breakpoint: 1024,
                settings: { slidesToShow: 4, slidesToScroll: 1, infinite: true, dots: true }
            },
            {
                breakpoint: 768,
                settings: { slidesToShow: 2, slidesToScroll: 1, initialSlide: 1 }
            },
            {
                breakpoint: 480,
                settings: { slidesToShow: 1, slidesToScroll: 1, variableWidth: false, }
            }
        ]
    };

    return (
        <div>
            <Slider {...settings}>

                {topMeel.map((item) => <CarouselItem
                    image={item.image}
                    title={item.title}
                />)}

            </Slider>

        </div>
    )
}

export default MultItemCarousel
