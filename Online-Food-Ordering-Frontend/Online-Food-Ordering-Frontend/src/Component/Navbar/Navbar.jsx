import { Avatar, Badge, IconButton } from "@mui/material"
import SearchIcon from '@mui/icons-material/Search';
import { pink } from "@mui/material/colors";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';



export const Navbar = () => {
    return (
        <div className="px-5 z-50 py-[.8rem] bg-[#e91e63] lg:px-20  flex justify-between">

            <div className="lg:mr-10 cursor-pointer flex items-center">
                <li className="logo list-none font-semibold text-gray-300  text-2xl">
                    ¡Qué Chido!
                </li>
            </div>

            <div className=" flex items-center space-x-2 lg:space-x-10 ">

                <div className="">
                    <IconButton>
                        <SearchIcon sx={{ fontSize: "1.5rem" }} />
                    </IconButton>
                </div>

                <div className="">
                    <Avatar sx={{ bgcolor: "white", color: pink.A400 }}>
                        C
                    </Avatar>
                </div>

                <div className="">
                    <IconButton>
                        <Badge badgeContent={4} slotProps={{
                            badge: {
                                style: {
                                    backgroundColor: 'black',
                                    color: 'white'
                                }
                            }
                        }}>
                            <ShoppingCartIcon sx={{ fontSize: "1.5rem" }} />

                        </Badge>
                    </IconButton>
                </div>
            </div>

        </div>
    )
}


