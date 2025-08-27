"use client";

import { useState } from "react";
import { IoSearch } from "react-icons/io5";

type SearchEntity = {
	text: string;
	image: string;
	link: string;
};

export default function SearchBar() {
	const [inputValue, setInputValue] = useState("");
	const [suggestion, setSuggestions] = useState<string[]>([
		"React for Beginners",
		"Python Basics",
		"JavaScript Essentials",
	]);
	const [entities, setEntities] = useState<SearchEntity[]>([
		{
			text: "React - The Complete Guide",
			image:
				"https://img.favpng.com/24/2/12/js-icon-logo-icon-react-icon-png-favpng-V4GKq1D3n3V713pYyrHeAERdm.jpg",
			link: "/course/react-the-complete-guide",
		},
		{
			text: "Angela Yu",
			image:
				"https://miro.medium.com/1*8OkdLpw_7VokmSrzwXLnbg.jpeg",
			link: "/course/react-the-complete-guide",
		},
	]);

	const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const value = e.target.value;
		setInputValue(value);
	};

	return (
		<div className="flex items-center border gap-2 border-gray-300 rounded-full p-2 w-full lg:w-[70%] md:w-1/2 relative">
			<div className="flex items-center justify-center w-8 aspect-square bg-emerald-500 rounded-full">
				<IoSearch className="text-white text-xl" />
			</div>
			<input
				type="text"
				placeholder="Search ex. React, Python, JavaScript"
				className="w-full outline-none"
				value={inputValue}
				onChange={handleInputChange}
			/>
      {
        (suggestion.length > 0 || entities.length > 0) && (
          <div className="absolute top-[55px] left-0 bg-white w-full text-sm text-gray-500 px-4 py-2 border border-gray-300 rounded-lg p-5 z-10">
            {suggestion.length > 0 &&
              suggestion.map((item, index) => (
                <div className="flex items-center gap-3 mb-2 hover:bg-gray-100 h-14 px-2 rounded-lg">
                  <p key={index} className="  text-black font-medium">
                    {item}
                  </p>
                </div>
              ))}
            {entities.length > 0 &&
              entities.map((entity, index) => (
                <a
                  href={entity.link}
                  key={index}
                  className="flex items-center gap-3 mb-2 hover:bg-gray-100 h-14 px-2 rounded-lg"
                >
                  <img
                    src={entity.image}
                    alt={entity.text}
                    className="w-10 h-10 object-cover rounded-lg"
                  />
                  <p className="text-black font-medium">{entity.text}</p>
                </a>
              ))}
          </div>
        )
      }
		</div>
	);
}
