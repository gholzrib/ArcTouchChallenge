package gholzrib.arctouchchallenge.core.models;

import java.util.ArrayList;

/**
 * Created by Gunther Ribak on 01/12/2016.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class TMDBConfiguration {

    public static final String IMAGE_TYPE_ORIGINAL = "original";

    public static final int IMAGE_TYPE_BACKDROP = 0;
    public static final int IMAGE_TYPE_LOGO = 1;
    public static final int IMAGE_TYPE_POSTER = 2;
    public static final int IMAGE_TYPE_PROFILE = 3;
    public static final int IMAGE_TYPE_STILL = 4;

    ImagesConfiguration images;

    ArrayList<String> change_keys = new ArrayList<>();

    public static class ImagesConfiguration {

        String base_url;
        String secure_base_url;
        ArrayList<String> backdrop_sizes = new ArrayList<>();
        ArrayList<String> logo_sizes = new ArrayList<>();
        ArrayList<String> poster_sizes = new ArrayList<>();
        ArrayList<String> profile_sizes = new ArrayList<>();
        ArrayList<String> still_sizes = new ArrayList<>();

        public String getBase_url() {
            return base_url;
        }

        public void setBase_url(String base_url) {
            this.base_url = base_url;
        }

        public String getSecure_base_url() {
            return secure_base_url;
        }

        public void setSecure_base_url(String secure_base_url) {
            this.secure_base_url = secure_base_url;
        }

        public ArrayList<String> getBackdrop_sizes() {
            return backdrop_sizes;
        }

        public void setBackdrop_sizes(ArrayList<String> backdrop_sizes) {
            this.backdrop_sizes = backdrop_sizes;
        }

        public ArrayList<String> getLogo_sizes() {
            return logo_sizes;
        }

        public void setLogo_sizes(ArrayList<String> logo_sizes) {
            this.logo_sizes = logo_sizes;
        }

        public ArrayList<String> getPoster_sizes() {
            return poster_sizes;
        }

        public void setPoster_sizes(ArrayList<String> poster_sizes) {
            this.poster_sizes = poster_sizes;
        }

        public ArrayList<String> getProfile_sizes() {
            return profile_sizes;
        }

        public void setProfile_sizes(ArrayList<String> profile_sizes) {
            this.profile_sizes = profile_sizes;
        }

        public ArrayList<String> getStill_sizes() {
            return still_sizes;
        }

        public void setStill_sizes(ArrayList<String> still_sizes) {
            this.still_sizes = still_sizes;
        }
    }

    public ImagesConfiguration getImages() {
        return images;
    }

    public void setImages(ImagesConfiguration images) {
        this.images = images;
    }

    public ArrayList<String> getChange_keys() {
        return change_keys;
    }

    public void setChange_keys(ArrayList<String> change_keys) {
        this.change_keys = change_keys;
    }

    /**
     * Returns the most appropriate image size URL path
     *
     * @param imageType
     * @param imageWidth
     * @return
     */
    public String getImageSizeUrlPath(int imageType, int imageWidth) {
        ArrayList<String> requiredImageArray = new ArrayList<>();
        switch (imageType) {
            case IMAGE_TYPE_BACKDROP:
                requiredImageArray = images.getBackdrop_sizes();
                break;
            case IMAGE_TYPE_LOGO:
                requiredImageArray = images.getLogo_sizes();
                break;
            case IMAGE_TYPE_POSTER:
                requiredImageArray = images.getPoster_sizes();
                break;
            case IMAGE_TYPE_PROFILE:
                requiredImageArray = images.getProfile_sizes();
                break;
            case IMAGE_TYPE_STILL:
                requiredImageArray = images.getStill_sizes();
                break;
        }
        for (String s : requiredImageArray) {
            if (!s.equals(IMAGE_TYPE_ORIGINAL)) {
                int size = Integer.parseInt(s.replace("w", ""));
                if (size >= imageWidth) {
                    return s;
                }
            }
        }
        return null;
    }

}
