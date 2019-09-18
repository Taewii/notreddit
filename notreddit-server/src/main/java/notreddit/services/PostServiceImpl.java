package notreddit.services;

import notreddit.domain.models.requests.PostCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    @Override
    public ResponseEntity<?> create(PostCreateRequest request) {
        // TODO: 18.9.2019 г.
        return null;
    }
}
