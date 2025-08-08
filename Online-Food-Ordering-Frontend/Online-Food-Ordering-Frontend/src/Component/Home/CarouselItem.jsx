

const CarouselItem = ({ image, title }) => {
    return (
        <div className="flex flex-col justify-center items-center p-4">
            <img src={image} alt="" className="w-[10rem] h-[10rem] lg:h-[14rem] lg:w-[14rem] 
            rounded-full object-cover object-center" />

            <span className="mt-4 px-2 font-semibold text-xl lg:text-2xl text-gray-400 text-center truncate max-w-[12rem] lg:max-w-[16rem] hover:text-clip hover:overflow-visible hover:whitespace-normal">{title}</span>
        </div>
    )
}



export default CarouselItem