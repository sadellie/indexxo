import os
from pathlib import Path
from dataclasses import dataclass


@dataclass
class FileObject():
    full_path: str = ""
    full_name: str = ""
    name: str = ""
    size: int = 0  # Bytes
    extension: str | None = None
    type: str = ""


@dataclass
class Indexxo():
    indexed_files: dict[Path, FileObject]
    filetypes: dict
    my_spaces: list[FileObject]

    def add_space(self, path: Path, ignore: list[Path]):
        self._discover(path, ignore)
        return

    def remove_space(self, path: Path):
        # new_dict: dict[Path, FileObject] = {}
        copied_dict = self.indexed_files.copy()

        for file_path, file_object in copied_dict.items():
            if file_path.is_relative_to(path):
                self.indexed_files.pop(file_path)
        return

    def get_spaces(self) -> list[FileObject]:
        # return self.get_content(str(self.space_paths))[0]
        return self.my_spaces

    def find(self, path: Path) -> FileObject | None:
        try:
            return self.indexed_files[path]
        except KeyError:
            return None

    def get_content(self, path: str | Path) -> tuple[list[FileObject], FileObject | None]:
        path = Path(path)

        def by_path(file_object: FileObject) -> bool:
            return path == Path(file_object.full_path).parent

        return list(filter(by_path, self.indexed_files.values())), self.find(path.parent)

    def _discover(self, path: Path, ignore: list[Path]):
        files_in_path: dict[Path, FileObject] = {}

        for directory, folders, files in os.walk(path, topdown=False):
            # We are in directory which has multiple folders and files in it
            directory: Path = Path(directory)
            folders: list[str]
            files: list[str]

            # We are in directory that needs to be ignored = skipping and moving on to next directory
            if directory in ignore: continue

            # All files and folders in this directory
            files_in_directory: dict[Path, FileObject] = {}

            # Loading files
            files_in_directory = self._process_files_in_directory(directory, files)

            # Loading folders. Need it so that size of directory includes folders.
            for folder in folders:
                full_folder_path = directory / Path(folder)
                # Skip ignored folder
                if full_folder_path in ignore: continue
                # Looking for folder from this directory in dictionary of already added folders
                found_folder = files_in_path[full_folder_path]
                files_in_directory[full_folder_path] = found_folder

            # Loading this directory
            directory_object = FileObject(
                full_path=str(directory),
                full_name=os.path.basename(directory),
                name=os.path.basename(directory),
                size=sum(i.size for i in files_in_directory.values()),
                type="folder"
            )

            files_in_path.update(files_in_directory)
            files_in_path[directory / Path(directory_object.full_path)] = directory_object

        self.indexed_files.update(files_in_path)
        files_in_path[path].type = "space"
        self.my_spaces.append(files_in_path[path])

    def _process_files_in_directory(self, directory_path: Path, files: list[str]) -> dict[Path, FileObject]:
        all_file_objects_in_directory: dict[Path, FileObject] = {}
        for file in files:
            file_path = directory_path / Path(file)
            name, ext = os.path.splitext(file)

            file_obj = FileObject(
                full_path=str(file_path),
                full_name=file_path.name,
                name=name,
                extension=ext,
                type=self._get_file_type(ext[1:]),
                size=file_path.stat().st_size
            )
            all_file_objects_in_directory[Path(file_obj.full_path)] = file_obj

        return all_file_objects_in_directory

    def _get_file_type(self, ext):
        try:
            return self.filetypes[ext]
        except KeyError:
            return "other"

# ALL File types
# space Indexxo space
# image Images
# document Sheet, Docs, Slide, Codes
# video Videos
# archive Archives
# audio Audio
# program Apps
