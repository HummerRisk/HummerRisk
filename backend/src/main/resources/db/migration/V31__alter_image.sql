
DROP TABLE image_grype_table;
DROP TABLE image_grype_json;
DROP TABLE image_syft_table;
DROP TABLE image_syft_json;

ALTER TABLE image_result DROP grype_table;
ALTER TABLE image_result DROP grype_json;
ALTER TABLE image_result DROP syft_table;
ALTER TABLE image_result DROP syft_json;
ALTER TABLE image_result DROP scan_type;

ALTER TABLE history_image_task DROP grype_table;
ALTER TABLE history_image_task DROP grype_json;
ALTER TABLE history_image_task DROP syft_table;
ALTER TABLE history_image_task DROP syft_json;
ALTER TABLE history_image_task DROP scan_type;
