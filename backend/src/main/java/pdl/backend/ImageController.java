package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {
    Optional<Image> optionalImage = imageDao.retrieve(id);

    if (optionalImage.isPresent()) {
      // If the image is found, return a ResponseEntity with the image data
      Image image = optionalImage.get();
      return ResponseEntity
          .ok()
          .contentType(MediaType.IMAGE_JPEG)
          .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
    } else {
      // If the image is not found, return a ResponseEntity with a not found status
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {

    Optional<Image> optionalImage = imageDao.retrieve(id);
    if (optionalImage.isPresent()) {
      Image image = optionalImage.get();
      imageDao.delete(image);
      return new ResponseEntity<>("Image correctement supprimée\n", HttpStatus.OK);
    }
    else{
      return new ResponseEntity<>("Image non trouvée\n", HttpStatus.NOT_FOUND);
  }
}

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) {
    try {
      // Vérifie si le fichier est vide
      if (file.isEmpty()) {
        return new ResponseEntity<>("Le fichier est vide.", HttpStatus.BAD_REQUEST);
      }

      // Crée une instance de la classe Image avec les données du fichier
      Image newImage = new Image(file.getOriginalFilename(), file.getBytes());

      // Ajoute l'image à ImageDao
      imageDao.create(newImage);

      return new ResponseEntity<>("L'image a été ajoutée avec succès.\n", HttpStatus.CREATED);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("Une erreur s'est produite lors de l'ajout de l'image.\n",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();
    List<Image> imgList = imageDao.retrieveAll();

    for (Image image : imgList){
      ObjectNode imgNode = mapper.createObjectNode();
      imgNode.put("name", image.getName());
      imgNode.put("id", image.getId());
      nodes.add(imgNode);
    }
    return nodes;
  }

}
