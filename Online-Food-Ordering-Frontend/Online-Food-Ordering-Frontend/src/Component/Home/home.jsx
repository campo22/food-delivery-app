import "./home.css";
import MultItemCarousel from './MultItemCarousel';

const Home = () => {
    return (
        <div>

            <section className="banner z-50 relative flex items-center justify-center ">

                <div className="w-[50vw] z-10 text-center">

                    <p className="text-5xl font-bold text-[#fdf9fd] mb-4 animate-pulse">¡Qué Chido!</p>
                    <p className=" z-10 text-2xl text-[#fffbfb] font-semibold italic shadow-lg drop-shadow-lg ">
                        Auténticos sabores mexicanos que te harán decir… <span className="text-[#ef14b8]">¡Qué Chido!</span>
                    </p>
                </div>

                <div className="cover absolute top-0 left-0 right-0 ">

                </div >

                <div className="fadout ">

                </div>

            </section>

            <section className=" p-10 lg:py-10 lg:px-20 ">
                <p className="text-2xl font-semibold text-gray-400  py-3 pb-10"> Top Meals</p>
                <MultItemCarousel />
            </section>

            <section className="px-3  lg:px-20">
                <h1 className="text-2xl font-semibold text-gray-400  py-3 pb-10"> Ordene en los mejores restaurantes </h1>
            </section>

            <div>

            </div>

        </div>
    );
};

export default Home;

