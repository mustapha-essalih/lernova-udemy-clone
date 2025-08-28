"use client";

import { useState, useEffect } from "react";
import { IoSearch } from "react-icons/io5";
import axios from "axios"

type SearchEntity = {
	text: string;
	image: string;
	link: string;
};

interface SearchResponse {
	success: boolean;
	message: string;
}

interface SuggestionResponse extends SearchResponse {
	data: string[];
}

interface InstructorsResponse extends SearchResponse {
	data: any[];
}

/**
 * 
 * @returns 
 * /api/v1/search/suggest?prefix=github
/api/v1/search/instructors/suggest?prefix=
/api/v1/search/titles/suggest?prefix
/api/v1/search/courses?q=
http://localhost:8081/api/v1/search/courses?q=java&rating=4.5&languages=ENGLISH&languages=FRENCH&subcategories=programming&levels=beginner&levels=advanced&priceTypes=free&priceTypes=paid&page=1&size=10
 */

export default function SearchBar() {
	let debounceTimeout: ReturnType<typeof setTimeout>;
	let controller: AbortController | null = null;
	const [inputValue, setInputValue] = useState("");
	const [suggestion, setSuggestions] = useState<string[]>([]);
	const [entities, setEntities] = useState<SearchEntity[]>([]);

	const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const value = e.target.value;
		setInputValue(value);

		clearTimeout(debounceTimeout);

		debounceTimeout = setTimeout(async () => {
			controller?.abort();
			controller = new AbortController();

			const urls = [
				`http://localhost:8081/api/v1/search/suggest?prefix=${value}`,
				`http://localhost:8081/api/v1/search/instructors/suggest?prefix=${value}`,
				// `http://localhost:8081/api/v1/search/titles/suggest?prefix=${value}`
			];

			try {
				const [res1, res2] = await Promise.all(
					urls.map(url => axios.get(url, { signal: controller!.signal }))
				);

				const suggestionsData = res1.data as SuggestionResponse;
				// const instructorsData = res2.data as InstructorsResponse;
				if (suggestionsData.success && suggestionsData.data) {
					setSuggestions(suggestionsData.data);
				} else {
					setSuggestions([]);
				}
			} catch (e: any) {
				if (e.name === "CanceledError") {
					console.log("Previous requests canceled");
				} else {
					console.error(e);
				}
			}
		}, 1500);
	};

	useEffect(() => {
  	return () => controller?.abort();
	}, []);

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
                <div key={index} className="flex items-center gap-3 mb-2 hover:bg-gray-100 h-14 px-2 rounded-lg">
                  <p className="  text-black font-medium">
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
