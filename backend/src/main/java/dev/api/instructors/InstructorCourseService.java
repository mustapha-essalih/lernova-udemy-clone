package dev.api.instructors;
 
import dev.api.courses.repository.redis.CacheVideosRepository;
import dev.api.courses.responses.CacheCourseResponse;
import dev.api.courses.responses.FilesResponses;
import dev.api.courses.responses.SectionsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import dev.api.courses.model.Courses;
import dev.api.courses.model.MainCategories;
import dev.api.courses.model.Subcategories;
import dev.api.courses.model.redis.CacheCourse;
import dev.api.courses.model.redis.CacheResources;
import dev.api.courses.model.redis.CacheSections;
import dev.api.courses.model.redis.CacheVideos;
import dev.api.courses.repository.CoursesRepository;
import dev.api.courses.repository.redis.CacheCourseRepository;
import dev.api.courses.repository.redis.CacheResourcesRepository;
import dev.api.courses.repository.redis.CacheSectionsRepository;
import dev.api.instructors.model.Instructors;
import dev.api.instructors.repository.InstructorsRepository;
import dev.api.instructors.request.CourseInitRequest;
import dev.api.instructors.request.ResourcesInitRequest;
import dev.api.instructors.request.SectionsInitRequest;
import dev.api.instructors.request.VideosInitRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class InstructorCourseService {


    private CacheCourseRepository cacheCourseRepository;
    private CacheSectionsRepository cacheSectionsRepository;
    private CacheVideosRepository cacheVideosRepository;
    private CacheResourcesRepository cacheResourcesRepository;
    private CoursesRepository coursesRepository;
    private InstructorsRepository instructorsRepository;


    public CacheCourseResponse init(CourseInitRequest request , String username) {
         
        String courseId = "course_" + UUID.randomUUID().toString();
        CacheCourse cacheCourse = new CacheCourse();
        CacheCourseResponse cacheCourseResponse = new CacheCourseResponse();
        List<CacheSections> lCacheSections = new ArrayList<>(); 

        cacheCourse.setId(courseId);
        cacheCourse.setTitle(request.getTitle());
        cacheCourse.setSubTitle(request.getSubTitle());
        cacheCourse.setDescription(request.getDescription());
        cacheCourse.setPrice(request.getPrice());
        cacheCourse.setIsFree(request.isFree());
        cacheCourse.setCourseDurationMinutes(request.getCourseDurationMinutes());
        cacheCourse.setLanguage(request.getLanguage());
        cacheCourse.setStatus(request.getStatus());
        cacheCourse.setLevel(request.getLevel());

        Courses courses = new Courses();

        courses.setTitle(request.getTitle());
        courses.setSubTitle(request.getSubTitle());
        courses.setDescription(request.getDescription());
        courses.setPrice(request.getPrice());
        courses.setIsFree(false);
        courses.setCourseDurationMinutes(request.getCourseDurationMinutes());
        courses.setLanguage(request.getLanguage());
        courses.setStatus(request.getStatus());
        courses.setLevel(request.getLevel());
        Instructors instructor = instructorsRepository.findByUsername("sihadev734@skateru.com").get();
        courses.setInstructor(instructor);
        instructor.getCourses().add(courses);

        instructorsRepository.save(instructor);

        // cacheCourse.setCategory(request.getCategory()); // client should send list of categories


        // for (MainCategories mainCategorie : request.getMainCategories()) {
        //     if(mainCategorie.getName() != null){
        //         cacheCourse.getCategory().add(mainCategorie.getName());
        //     }
        //     for (Subcategories subcategorie : mainCategorie.getSubcategories()) {
        //         cacheCourse.getCategory().add(subcategorie.getName());
        //     }
        // }


        cacheCourseResponse.setCourseId(courseId);

        for (SectionsInitRequest sectionRequest : request.getSections()) {
            String sectionId = "section_" + UUID.randomUUID().toString();
            CacheSections cacheSection = new CacheSections();
            cacheSection.setId(sectionId);
            cacheSection.setTitle(sectionRequest.getTitle());

            SectionsResponse sectionsResponse = new SectionsResponse();
            sectionsResponse.setSectionId(sectionId);
            sectionsResponse.setTempId(sectionId);
            cacheCourseResponse.getSections().add(sectionsResponse);

            if (sectionRequest.getVideos() != null) {
                List<CacheVideos> lCacheVideos = new ArrayList<>();

                for (VideosInitRequest videoRequest : sectionRequest.getVideos()) {
                    String videoId = "video_" + UUID.randomUUID().toString();
                    CacheVideos cacheVideo = new CacheVideos();
                    cacheVideo.setId(videoId);
                    cacheVideo.setTitle(videoRequest.getTitle());
                    cacheVideo.setDurationMinutes(videoRequest.getDurationMinutes());
                    cacheVideo.setPreview(videoRequest.isPreview());
                    
                    FilesResponses filesResponses = new FilesResponses();
                    filesResponses.setFileId(videoId);
                    filesResponses.setKind("VIDEO");
                    filesResponses.setTempId(videoId);
                    
                    cacheCourseResponse.getFiles().add(filesResponses);

                    cacheSection.getVideoIds().add(videoId);
                    
                    lCacheVideos.add(cacheVideo);
                }
                cacheVideosRepository.saveAll(lCacheVideos);
            }

            if (sectionRequest.getResources() != null) {
                List<CacheResources> lCacheResources = new ArrayList<>();

                for (ResourcesInitRequest resourceRequest : sectionRequest.getResources()) {
                    String resourceId = "resource_" + UUID.randomUUID().toString();
                    CacheResources cacheResource = new CacheResources();
                    cacheResource.setId(resourceId);
                    cacheResource.setTitle(resourceRequest.getTitle());
                    cacheResource.setPreview(resourceRequest.isPreview());

                    FilesResponses filesResponses = new FilesResponses();
                    filesResponses.setFileId(resourceId);
                    filesResponses.setKind("RESOURCE");
                    filesResponses.setTempId(resourceId);

                    cacheCourseResponse.getFiles().add(filesResponses);
                    
                    cacheSection.getResourceIds().add(resourceId);
                    
                    lCacheResources.add(cacheResource);
                }
                cacheResourcesRepository.saveAll(lCacheResources);
            }
            cacheCourse.getSectionIds().add(sectionId);

            lCacheSections.add(cacheSection);
        }
        cacheSectionsRepository.saveAll(lCacheSections);

        cacheCourseRepository.save(cacheCourse);
 
        return cacheCourseResponse;
    }
 
    

}
